package com.openclassrooms.hexagonal.games

import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.screen.homefeed.HomefeedViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class HomefeedViewModelTest {

    private lateinit var mockPostRepository: PostRepository
    private lateinit var viewModel: HomefeedViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockPostRepository = mockk() //mockk du repository
        every { mockPostRepository.posts } returns flowOf( //every simule la méthode .posts à test repositoryMoké et retourne une listof avec les posts 1 et2
            listOf(
                Post("1", "post1", "testURL", "testURL", 123456789034, author = null),
                Post("2", "post2", "testURL", "testURL", 123121243432, author = null)
            )
        )
        viewModel = HomefeedViewModel(mockPostRepository)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLoadPostsFromRepository() = runTest {
        //simulation de posts
        val expectedPosts = listOf(
            Post("1", "post1", "testURL", "testURL", 123456789034, author = null),
            Post("2", "post2", "testURL", "testURL", 123121243432, author = null))
        //sim methode posts
        every { mockPostRepository.posts } returns flowOf(expectedPosts)

        viewModel = HomefeedViewModel(mockPostRepository)
        //exec du dispatch / sched pour coroutine en attente
        testDispatcher.scheduler.advanceUntilIdle()
        //verif
        assertEquals(expectedPosts,viewModel.posts.value)
        //verif que la méthode post du repo a été appelée au - une fois.
        verify(atLeast = 1) { mockPostRepository.posts }
    }
}