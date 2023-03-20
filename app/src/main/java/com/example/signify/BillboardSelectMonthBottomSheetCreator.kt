package com.example.signify

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

class BillboardSelectMonthBottomSheetCreator : BottomSheetCreator {
    override fun createBottomSheet(bottomSheet: View): BottomSheetBehavior<View> {
        return  BottomSheetBehavior.from(bottomSheet)
    }
}