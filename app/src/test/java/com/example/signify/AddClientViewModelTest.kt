package com.example.signify

import AddClientViewModel
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.signify.repository.FirestoreRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class AddClientViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: FirestoreRepository

    private lateinit var viewModel: AddClientViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = AddClientViewModel()
    }

    @Test
    fun `addNewClient with valid parameters`() {
        val email = "test@example.com"
        val password = "testpassword"
        val managerUid = "managerUid"
        val name = "test client"

        viewModel.addNewClient(email, password, managerUid, name)

        Mockito.verify(repository).addNewClient(email, password, managerUid, name)
    }
}
