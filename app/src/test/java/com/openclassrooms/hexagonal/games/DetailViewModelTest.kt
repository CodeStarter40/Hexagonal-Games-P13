package com.openclassrooms.hexagonal.games

import com.openclassrooms.hexagonal.games.data.repository.CommentRepository
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.screen.detail.DetailViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import io.mockk.verify
import junit.framework.TestCase.assertEquals


@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private lateinit var viewModel: DetailViewModel
    private val mockPostRepository: PostRepository = mockk()
    private val mockCommentRepository: CommentRepository = mockk() //mockk() génére un mock du CommentRepo ici

    @Before
    fun setUp() {
        kotlinx.coroutines.Dispatchers.setMain(StandardTestDispatcher())
        viewModel = DetailViewModel(mockPostRepository, mockCommentRepository) //init vM avec les mock
    }

    @After
    fun tearDown() {
        //reset du main dispatcher
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @Test
    fun getPostUpdatesPostStateFlow()= runTest {
        //simulation de post
        val postId = "1"
        val post = Post(
            postId,
            "Titre",
            "Description du post",
            "testURL",
            timestamp = 11123213,
            null,
            null)
        every { mockPostRepository.posts } returns flowOf(listOf(post)) //every simule la méthode "posts" à test repositoryMoké et retourne une liste de post

        //1 call de la méthode à test
        viewModel.getPost(postId)
        //2 dispatcher pour exec coroutine
        StandardTestDispatcher().scheduler.advanceUntilIdle()
        //3 check MAJ stateflow
        assertEquals(post, viewModel.post.value)
        //4 check que la méthode a bien été appelée
        verify { mockPostRepository.posts }
    }

    @Test
    fun loadCommentsUpdatesCommentsStateFlow() = runTest {
        //simalation de com
        val postId = "1"
        val comments = listOf(
            com.openclassrooms.hexagonal.games.domain.model.Comment(
                "1",
                postId,
                "John",
                content = "Commentaire 1",
                uid = "123",
                timestamp = null
            ),
            com.openclassrooms.hexagonal.games.domain.model.Comment(
                "2",
                postId,
                "Jane",
                content = "Commentaire 2",
                uid = "456",
                timestamp = null
            ))
        every { mockCommentRepository.getComments(postId) } returns flowOf(comments) //every simule la méthode "getComments" à test repositoryMoké et retourne un le flow de com
        //2 call de la méthode à test
        viewModel.loadComments(postId)
        //3 dispatcher pour exec coroutine
        StandardTestDispatcher().scheduler.advanceUntilIdle()
        //4 check MAJ stateflow
        assertEquals(comments, viewModel.comments.value)
        //5 check que la méthode a bien été appelée
        verify { mockCommentRepository.getComments(postId) }

    }
}