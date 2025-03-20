package com.ravi.skinhealthyai.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ravi.skinhealthyai.R
import com.ravi.skinhealthyai.data.model.CarouselItem
import com.ravi.skinhealthyai.data.model.History
import com.ravi.skinhealthyai.databinding.FragmentHomeBinding
import com.ravi.skinhealthyai.ui.ViewModelFactory
import com.ravi.skinhealthyai.ui.adapter.CarouselAdapter
import com.ravi.skinhealthyai.ui.adapter.LastHistoryAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager2: ViewPager2
    private lateinit var pageChangeListener: ViewPager2.OnPageChangeCallback
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private val delayTime: Long = 3000
    private var currentPage = 0
    private var isAutoPlay = true
    private var isUserScrolling = false

    private val params = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(8, 0, 8, 0)
    }

    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            if (_binding == null) return@launch
            setupUI()
            setupObserve()
            viewModel.fetchLastThreeHistory()
        }
    }

    private fun setupUI() {
        if (_binding == null) return
        setupCarousel()
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.showHistory.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToHistoryFragment())
        }
    }

    private fun setupCarousel() {
        if (_binding == null) return

        viewPager2 = binding.viewPager
        val slideDotLL = binding.slideDotLL

        val items = listOf(
            CarouselItem(R.drawable.bg_carousel_1, "Gejala Penyakit Kulit"),
            CarouselItem(R.drawable.bg_carousel_2, "Kulit sehat dan elegan"),
            CarouselItem(R.drawable.bg_carousel_3, "Rawatlah kulit Anda sekarang juga")
        )

        val adapter = CarouselAdapter(items)
        viewPager2.adapter = adapter
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val dotImage = Array(items.size) { ImageView(requireContext()) }
        dotImage.forEach {
            it.setImageResource(R.drawable.ic_indicator)
            slideDotLL.addView(it, params)
        }
        dotImage[0].setImageResource(R.drawable.ic_indicator_active)

        pageChangeListener = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (_binding == null) return

                dotImage.mapIndexed { index, imageView ->
                    imageView.setImageResource(
                        if (position == index) R.drawable.ic_indicator_active else R.drawable.ic_indicator
                    )
                }

                currentPage = position
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    isUserScrolling = true
                    stopAutoPlay()
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    isUserScrolling = false

                    // Jika user scroll manual ke kanan setelah halaman terakhir, kembali ke index 0 dengan smooth scroll
                    if (viewPager2.currentItem == items.size - 1) {
                        viewPager2.post {
                            smoothScrollToItem(0)
                            handler.postDelayed({ startAutoPlay() }, 1000)
                        }
                    } else {
                        startAutoPlay()
                    }
                }
            }
        }

        viewPager2.registerOnPageChangeCallback(pageChangeListener)

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            if (isAutoPlay) {
                currentPage = if (currentPage == items.size - 1) 0 else currentPage + 1
                smoothScrollToItem(currentPage)
                handler.postDelayed(runnable, delayTime)
            }
        }

        startAutoPlay()
    }

    private fun startAutoPlay() {
        if (!isUserScrolling) {
            isAutoPlay = true
            handler.postDelayed(runnable, delayTime)
        }
    }

    private fun stopAutoPlay() {
        isAutoPlay = false
        handler.removeCallbacks(runnable)
    }

    /**
     * Method untuk memberikan efek smooth scrolling pada ViewPager2.
     */
    private fun smoothScrollToItem(index: Int) {
        val recyclerView = viewPager2.getChildAt(0) as? RecyclerView
        recyclerView?.smoothScrollToPosition(index)
    }

    private fun setupObserve() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            showEmptyText(isEmpty)
            showDataList(isEmpty)
        }

        viewModel.lastThreeHistory.observe(viewLifecycleOwner) { data ->
            getLastHistory(data)
        }
    }

    private fun getLastHistory(data: List<History>) {
        if (_binding == null) return

        val adapter = LastHistoryAdapter(object : LastHistoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: History) {
                showSelectedHistory(data)
            }
        })
        binding.rvHistory.adapter = adapter
        adapter.submitList(data)
    }

    private fun showSelectedHistory(history: History) {
        val action = HomeFragmentDirections.actionNavigationHomeToNavigationDetailHistory(history.id)
        findNavController().navigate(action)
    }

    private fun showLoading(isLoading: Boolean) {
        if (_binding != null) {
            binding.progresBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showEmptyText(isEmpty: Boolean) {
        if (_binding != null) {
            binding.textNotFound.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    private fun showDataList(isEmpty: Boolean) {
        if (_binding != null) {
            binding.rvHistory.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewPager2.unregisterOnPageChangeCallback(pageChangeListener)
        stopAutoPlay()
    }
}
