package com.example.signify

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.signify.repository.FirestoreRepository
import com.google.common.base.Verify.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AccountViewModelTest {

    // Allows LiveData to work properly in JUnit tests
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    // Mock the FirestoreRepository
    @Mock
    private lateinit var repository: FirestoreRepository

    private lateinit var viewModel: AccountViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = AccountViewModel()
    }

    @Test
    fun signOut() {
        viewModel.signOut()

        verify(repository).signOut()
    }

    @Test
    fun getUserName() {
        val currentUserUid = "12345"
        val expectedName = "John Doe"

        // Create a LiveData observer to check the returned name
        val observer = mock(Observer::class.java) as Observer<String>

        // Set up the mock repository to return the expected name
        `when`(repository.getUserName(currentUserUid)).thenReturn(MutableLiveData(expectedName))

        // Observe the LiveData returned by the ViewModel
        viewModel.getUserName(currentUserUid).observeForever(observer)

        // Verify that the observer was called with the expected name
        verify(observer).onChanged(expectedName)
    }
}
