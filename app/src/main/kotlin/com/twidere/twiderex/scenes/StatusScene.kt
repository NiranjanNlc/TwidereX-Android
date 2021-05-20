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
package com.twidere.twiderex.scenes

import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.ErrorPlaceholder
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.status.DetailedStatusComponent
import com.twidere.twiderex.component.status.StatusDivider
import com.twidere.twiderex.component.status.TimelineStatusComponent
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.utils.generateNotificationEvent
import com.twidere.twiderex.viewmodel.StatusViewModel
import androidx.compose.ui.platform.LocalConfiguration

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StatusScene(
    statusKey: MicroBlogKey,
) {
    val account = LocalActiveAccount.current ?: return
    val appBarHeight = 56
    val availableHeight = LocalConfiguration.current.screenHeightDp - appBarHeight
    var spacerHeight by remember { mutableStateOf(0) }
    val viewModel = assistedViewModel<StatusViewModel.AssistedFactory, StatusViewModel>(
        statusKey,
        account,
    ) {
        it.create(account = account, statusKey = statusKey)
    }
    val source = viewModel.source.collectAsLazyPagingItems()
    val status by viewModel.status.observeAsState(initial = null)

    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    title = {
                        Text(text = stringResource(id = R.string.scene_search_tabs_tweets))
                    },
                    navigationIcon = {
                        AppBarNavigationButton()
                    }
                )
            }
        ) {
            if (source.loadState.refresh is LoadState.Loading || source.loadState.refresh is LoadState.Error) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier.verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    status?.let {
                        DetailedStatusComponent(data = it)
                    }
                    Divider()
                    when (val refresh = source.loadState.refresh) {
                        is LoadState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .heightIn(min = ButtonDefaults.MinHeight)
                                        .padding(ButtonDefaults.ContentPadding),
                                )
                            }
                        }
                        is LoadState.Error -> {
                            ErrorPlaceholder(throwable = refresh.error.generateNotificationEvent())
                        }
                        else -> Unit
                    }
                }
            }
            if (
                source.loadState.refresh is LoadState.NotLoading && source.itemCount > 0
            ) {
                val distance = with(LocalDensity.current) {
                    -50.dp.toPx()
                }
                val index = remember {
                    for (i in 0..source.itemCount) {
                        if (source.peek(i)?.statusKey == status?.statusKey) {
                            return@remember i
                        }
                    }
                    0
                }
                val state = rememberLazyListState(
                    initialFirstVisibleItemIndex = index,
                )
                LaunchedEffect(Unit) {
                    if (index != 0 && state.firstVisibleItemIndex == index && state.firstVisibleItemScrollOffset == 0) {
                        state.animateScrollBy(distance, tween())
                        state.animateScrollBy(-distance, tween())
                    }
                }
                LazyColumn(
                    state = state,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (source.loadState.refresh is LoadState.Loading || source.loadState.refresh is LoadState.Error) {
                        status?.let {
                            item(key = it.hashCode()) {
                                DetailedStatusComponent(data = it)
                            }
                        }
                        if (source.loadState.refresh is LoadState.Loading) {
                            item {
                                CircularProgressIndicator()
                            }
                        }
                    } else {
                        itemsIndexed(source) { index, it ->
                            it?.let { status ->
                                Column {
                                    Column(Modifier.layout { measurable, constraints ->
                                        val placeable = measurable.measure(constraints)
                                        layout(placeable.width, placeable.height) {
                                            if (index == source.itemCount - 1) {
                                                val lastItemHeight =
                                                    placeable.measuredHeight.toDp().value.toInt()
                                                spacerHeight =
                                                    availableHeight - (lastItemHeight % availableHeight)
                                            }
                                            placeable.placeRelative(0, 0)
                                        }
                                    }) {
                                        if (status.statusKey == statusKey) {
                                            DetailedStatusComponent(data = status)
                                        } else {
                                            TimelineStatusComponent(data = status)
                                        }
                                        if (status.statusKey == statusKey) {
                                            Divider()
                                        } else {
                                            StatusDivider()
                                        }
                                    }
                                    if (index == source.itemCount - 1) {
                                        Spacer(
                                            modifier = Modifier.height(spacerHeight.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
