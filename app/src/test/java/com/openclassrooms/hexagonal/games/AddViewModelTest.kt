package com.openclassrooms.hexagonal.games

import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.screen.ad.AddViewModel
import com.openclassrooms.hexagonal.games.screen.ad.FormEvent
import com.openclassrooms.hexagonal.games.screen.ad.FormError
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import org.junit.After


import org.junit.Before
import org.junit.Test


class AddViewModelTest {

    private var postRepository: PostRepository = mockk()
    private lateinit var viewModel: AddViewModel

    @Before
    fun setup() {
        postRepository = mockk(relaxed = true)
        //init vM avec le mock
        viewModel = AddViewModel(postRepository)
    }

    @Test
    fun updatesPostTitle() {
        val newTitle = "New Title"
        viewModel.onAction(FormEvent.TitleChanged(newTitle))

        assertEquals(newTitle, viewModel.post.value.title)
    }


    @Test
    fun verifyPostReturnsTitleErrorWhenTitleIsEmpty() {
        viewModel.onAction(FormEvent.TitleChanged("")) //blank attendu

        val error = viewModel.verifyPost() //verifyPost doit retourner un TitleError
        assertEquals(FormError.TitleError, error)
    }

    @Test
    fun verifyPostReturnsTitleErrorWhenTitleIsTooShort() {
        viewModel.onAction(FormEvent.TitleChanged("V")) //too short

        val error = viewModel.verifyPost() //verifyPost doit retourner un TitleError
        assertEquals(FormError.TitleError, error)
    }


    @Test
    fun verifyPostReturnsNoErrorForValidTitle() {
        // Mettre à jour le titre dans le ViewModel
        viewModel.onAction(FormEvent.TitleChanged("Title")) //titre valide

        //verifyPost doit retourner null si le titre est valide
        val error = viewModel.verifyPost()
        assertNull(error)
    }
}
