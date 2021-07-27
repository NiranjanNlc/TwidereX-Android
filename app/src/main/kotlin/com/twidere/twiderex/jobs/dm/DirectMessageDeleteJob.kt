/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.jobs.dm

import com.twidere.services.microblog.DirectMessageService
import com.twidere.twiderex.model.DirectMessageDeleteData
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.DirectMessageRepository

class DirectMessageDeleteJob(
    private val repository: DirectMessageRepository,
    private val accountRepository: AccountRepository,
) {
    suspend fun execute(deleteData: DirectMessageDeleteData, accountKey: MicroBlogKey): Boolean {
        return try {
            val accountDetails = accountKey.let {
                accountRepository.findByAccountKey(accountKey = it)
            }?.let {
                accountRepository.getAccountDetails(it)
            } ?: return false
            repository.deleteMessage(
                accountKey = deleteData.accountKey,
                conversationKey = deleteData.conversationKey,
                messageId = deleteData.messageId,
                messageKey = deleteData.messageKey,
                service = accountDetails.service as DirectMessageService
            )
            true
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        }
    }
}
