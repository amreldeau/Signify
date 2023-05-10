package com.example.signify

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.signify.repository.FirestoreRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import java.text.SimpleDateFormat
import java.util.*

@ExtendWith(MockitoExtension::class)
class CancelBookingViewModelTest {

    @Mock
    private lateinit var repository: FirestoreRepository

    private lateinit var viewModel: CancelBookingViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = CancelBookingViewModel()
    }

    @Test
    fun testGetOrderDetails() {
        // Given
        val orderId = "123"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse("2023-05-09")
        val orderDetails = OrderDetails(
            "B1",
            124.0,
            "Abay",
            mapOf("2023month3" to true),
            "Pending",
            date
        )
        val liveData = MutableLiveData<OrderDetails>()
        liveData.value = orderDetails

        `when`(repository.getOrderDetails(orderId)).thenReturn(liveData)

        // When
        val result = viewModel.getOrderDetails(orderId)

        // Then
        assertNotNull(result)
        assertEquals(orderDetails, result.value)
    }
}
