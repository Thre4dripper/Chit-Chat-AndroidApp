package com.example.chitchatapp.ui.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chitchatapp.LottieStickers
import com.example.chitchatapp.adapters.StickersRecyclerAdapter
import com.example.chitchatapp.adapters.interfaces.StickerClickInterface
import com.example.chitchatapp.databinding.BsStickersBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StickersBottomSheet(private var stickerIndex: (Int) -> Unit) :
    BottomSheetDialogFragment(), StickerClickInterface {

    private lateinit var binding: BsStickersBinding
    private lateinit var stickerAdapter: StickersRecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BsStickersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bsStickersRv.apply {
            stickerAdapter = StickersRecyclerAdapter(this@StickersBottomSheet)
            adapter = stickerAdapter
        }

        stickerAdapter.submitList(LottieStickers.stickers)
    }

    override fun onStickerClick(stickerIndex: Int) {
        stickerIndex(stickerIndex)
        dismiss()
    }
}