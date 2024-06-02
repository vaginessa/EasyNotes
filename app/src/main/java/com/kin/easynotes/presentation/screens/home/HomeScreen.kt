package com.kin.easynotes.presentation.screens.home


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kin.easynotes.Notes
import com.kin.easynotes.domain.model.Note
import com.kin.easynotes.domain.usecase.viewModelFactory
import com.kin.easynotes.presentation.common.DeleteButton
import com.kin.easynotes.presentation.common.NotesButton
import com.kin.easynotes.presentation.common.NotesScaffold
import com.kin.easynotes.presentation.common.SearchButton
import com.kin.easynotes.presentation.common.SettingsButton
import com.kin.easynotes.presentation.common.TitleText
import com.kin.easynotes.presentation.navigation.NavRoutes
import com.kin.easynotes.presentation.screens.home.viewmodel.HomeModel
import com.kin.easynotes.presentation.screens.home.widgets.EmptyNoteList


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    navController: NavController,
    viewModel: HomeModel = viewModel<HomeModel>(factory = viewModelFactory { HomeModel(Notes.dataModule.noteRepository) })
) {
    NotesScaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                title = { TitleText(titleText = "Notes")},
                actions = {
                    if (viewModel.isSelectingMode.value) DeleteButton { viewModel.toggleIsDeleteMode(true) }
                    SearchButton { navController.navigate(NavRoutes.Search.route) }
                    SettingsButton { navController.navigate(NavRoutes.Settings.route) }
                }
            )
        },
        floatingActionButton = {
            NotesButton(
                text = "New Note",
                onClick = { navController.navigate(NavRoutes.Edit.route + "/0") }
            )
        },
        content =  {
            NoteList(navController = navController, viewModel)
        }
    )
}

@Composable
fun NoteList(navController: NavController, viewModel: HomeModel, searchText: String? = null) {
    var emptyText = "No created notes."
    val notesState by viewModel.getAllNotes.collectAsState(initial = listOf())
    val filteredNotes = if (searchText != null) {
        if (searchText == "") {
            emptyText = "No notes found."
            listOf()
        } else {
            notesState.filter { note ->
                note.name.contains(searchText, ignoreCase = true) ||
                        note.description.contains(searchText, ignoreCase = true)
            }
        }
    } else {
        notesState
    }

    when {
        filteredNotes.isEmpty() -> EmptyNoteList(emptyText)
        else -> NotesGrid(navController = navController, viewModel = viewModel, notes = filteredNotes)
    }

}
@Composable
fun NotesGrid(navController: NavController, viewModel: HomeModel, notes: List<Note>) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(12.dp)
    ) {
        items(notes) { note ->
            val slideDirection = if (notes.indexOf(note) % 2 == 0) -1 else 1
            val animVisibleState = remember {  MutableTransitionState(false).apply {  targetState = true  }  }
            AnimatedVisibility(
                visibleState = animVisibleState,
                enter =  fadeIn(animationSpec = tween(200)) +
                        scaleIn(
                            initialScale = 0.9f,
                            animationSpec = tween(200)
                        ),
                exit = slideOutHorizontally(
                    targetOffsetX = { slideDirection * it },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(durationMillis = 300))
            )  {
                NoteCard(
                    viewModel = viewModel,
                    note = note,
                    containerColor = when {
                        viewModel.selectedNotes.contains(note.id) -> MaterialTheme.colorScheme.surfaceContainerHighest
                        else ->  MaterialTheme.colorScheme.surfaceContainerHigh
                    },
                    onShortClick = {
                        when {
                            viewModel.isSelectingMode.value -> viewModel.toggleNoteSelection(note.id)
                            else -> navController.navigate(NavRoutes.Edit.route + "/${note.id}")
                        }
                    },
                    onLongClick = {
                        viewModel.toggleIsSelectingMode(true)
                        viewModel.toggleNoteSelection(note.id)
                    }
                )
                if (viewModel.isDeleteMode.value && viewModel.selectedNotes.contains(note.id)) {
                    animVisibleState.targetState = false
                }
            }
            if (!animVisibleState.targetState && !animVisibleState.currentState && viewModel.selectedNotes.contains(note.id)) {
                viewModel.toggleNoteSelection(note.id)
                animVisibleState.targetState = true
                viewModel.deleteNoteById(note.id)
            }
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteCard(viewModel: HomeModel, note: Note, containerColor : Color, onShortClick : () -> Unit, onLongClick : () -> Unit) {
    Box(
        modifier = Modifier
            .padding(bottom = 9.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .combinedClickable(
                onClick = { onShortClick() },
                onLongClick = { onLongClick() }
            )
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            if (note.name.isNotEmpty()) {
                Text(note.name)
            }
            if (note.description.isNotEmpty()) {
                Text(note.description)
            }
        }
    }
}