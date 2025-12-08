package com.study.ch11_jetpack

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.study.ch11_jetpack.databinding.FragmentOneBinding
import com.study.ch11_jetpack.databinding.ItemRecyclerviewBinding

class MyViewHolder(val binding: ItemRecyclerviewBinding): RecyclerView.ViewHolder(binding.root)

class MyAdapter(val datas: MutableList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding
        binding.itemData.text = datas[position]
    }

    override fun getItemCount(): Int {
        return datas.size
    }
}

class MyDecoration(val context: Context): RecyclerView.ItemDecoration() {
    // 모든 항목이 출력된 후 호출
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)

        // 리사이클러 뷰의 크기 계산
        val width = parent.width
        val height = parent.height

        // 이미지의 크기 계산 (drawable의 크기를 구할 때는 intrinsic 너비와 높이 사용)
        val image = ResourcesCompat.getDrawable(context.resources, R.drawable.kbo, null)
        val imageWidth = image?.intrinsicWidth
        val imageHeight = image?.intrinsicHeight

        // 이미지를 출력할 위치 계산
        val left = width / 2 - imageWidth?.div(2) as Int
        val top = height / 2 - imageHeight?.div(2) as Int

        // 원하는 위치에 이미지 출력
        canvas.drawBitmap(
            BitmapFactory.decodeResource(context.resources, R.drawable.kbo),
            left.toFloat(),
            top.toFloat(),
            null
        )
    }

    // 각 항목을 꾸미기 위해 호출
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        // 각 항목의 위치 획득
        val index = parent.getChildAdapterPosition(view) + 1

        if(index % 3 == 0){
            outRect.set(10, 10, 10, 60)
        }else {
            outRect.set(10, 10, 10, 0)
        }

        view.setBackgroundColor(Color.parseColor("#28a0ff"))
        ViewCompat.setElevation(view, 20.0f)
    }
}

class OneFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentOneBinding.inflate(inflater, container, false)

        val datas = mutableListOf<String>()
        for(i in 1..9){
            datas.add("item $i")
        }

        binding.recyclerview.layoutManager = LinearLayoutManager(activity)
        binding.recyclerview.adapter = MyAdapter(datas)
        binding.recyclerview.addItemDecoration(MyDecoration(activity as Context))

        return binding.root
    }
}