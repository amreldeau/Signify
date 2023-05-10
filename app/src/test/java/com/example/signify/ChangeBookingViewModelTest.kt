package com.example.signify

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.signify.repository.FirestoreRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@RunWith(MockitoJUnitRunner::class)
class ChangeBookingViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: FirestoreRepository

    private lateinit var viewModel: ChangeBookingViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = ChangeBookingViewModel()
    }

    @Test
    fun `getOrderById should return order with correct id`() {
        // Given
        val orderId = "order-123"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse("2023-05-09")
        val order = Order(orderId, "client-123", "billboard-123", mapOf("2023month3" to true), date, "pending", 100.0)
        val expected = MutableLiveData<Order>()
        expected.value = order
        Mockito.`when`(repository.getOrderById(orderId)).thenReturn(expected)

        // When
        val result = viewModel.getOrderById(orderId)

        // Then
        assertEquals(expected.value, result.value)
    }

    @Test
    fun `getBillboardAvailability should return correct availability for billboard`() {
        // Given
        val billboardId = "billboard-123"
        val availability = hashMapOf("2023-05-09" to true, "2023-05-10" to false)
        val expected = MutableLiveData<HashMap<String, Boolean>>()
        expected.value = availability
        Mockito.`when`(repository.getBillboardAvailability(billboardId)).thenReturn(expected)

        // When
        val result = viewModel.getBillboardAvailability(billboardId)

        // Then
        assertEquals(expected.value, result.value)
    }

    @Test
    fun `getPrice should return correct price for billboard`() {
        // Given
        val billboardId = "billboard-123"
        val price = 100.0
        val expected = MutableLiveData<Double>()
        expected.value = price
        Mockito.`when`(repository.getPrice(billboardId)).thenReturn(expected)

        // When
        val result = viewModel.getPrice(billboardId)

        // Then
        assertEquals(expected.value, result.value)
    }

    @Test
    fun `placeRequest should add request to repository`() {
        // Given
        val clientId = "client-123"
        val orderId = "order-123"
        val payoutDifference = 50.0
        val selected = mutableMapOf("2023-05-09" to true, "2023-05-10" to false)
        val type = "change"
        viewModel.placeRequest(clientId, orderId, payoutDifference, selected, type)

        // Then
        Mockito.verify(repository).addRequest(clientId, orderId, payoutDifference, selected, type)
    }
}
