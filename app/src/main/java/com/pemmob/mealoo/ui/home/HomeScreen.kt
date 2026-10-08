package com.pemmob.mealoo.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pemmob.mealoo.R
import com.pemmob.mealoo.ui.common.EmptyView
import com.pemmob.mealoo.ui.common.ErrorView
import com.pemmob.mealoo.ui.common.LoadingView
import com.pemmob.mealoo.ui.common.UiState
import com.pemmob.mealoo.ui.home.components.MealItem
import com.pemmob.mealoo.ui.home.components.RecipeSearchBar
import com.pemmob.mealoo.ui.theme.WarmCanvas

@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmCanvas,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_mealoo),
                    contentDescription = "Mealoo",
                    modifier = Modifier.height(34.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                RecipeSearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (val state = uiState) {
                    is UiState.Loading -> {
                        LoadingView()
                    }
                    is UiState.Empty -> {
                        EmptyView(message = state.message)
                    }
                    is UiState.Error -> {
                        ErrorView(
                            message = state.message,
                            onRetry = viewModel::retry
                        )
                    }
                    is UiState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            itemsIndexed(
                                items = state.data,
                                key = { _, meal -> meal.id }
                            ) { index, meal ->
                                MealItem(
                                    meal = meal,
                                    index = index,
                                    onClick = { onNavigateToDetail(meal.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
