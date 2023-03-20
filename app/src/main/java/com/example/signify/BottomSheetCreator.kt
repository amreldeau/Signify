package com.example.signify

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

interface BottomSheetCreator {
    fun createBottomSheet(bottomSheet: View): BottomSheetBehavior<View>
}