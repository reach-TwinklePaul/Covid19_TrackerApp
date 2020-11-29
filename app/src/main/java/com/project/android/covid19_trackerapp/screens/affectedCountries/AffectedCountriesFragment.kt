package com.project.android.covid19_trackerapp.screens.affectedCountries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.android.covid19_trackerapp.databinding.FragmentAffectedCountriesBinding
import com.project.android.covid19_trackerapp.network.DatabaseModel


class AffectedCountriesFragment : Fragment() {

    private lateinit var viewModel: AffectedCountriesViewModel

    private lateinit var binding: FragmentAffectedCountriesBinding
    private lateinit var adapter: CountryListAdapter

    private val countryList: MutableList<DatabaseModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(AffectedCountriesViewModel::class.java)

        binding = FragmentAffectedCountriesBinding.inflate(inflater)
        binding.countriesViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.loader.start()

        adapter = CountryListAdapter(countryList, CountryListAdapter.OnClickListener {
            viewModel.onApplyFilter(it)
        })
        binding.listView.adapter = adapter
        bindAdapter()

        viewModel.navigateToCountry.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                viewModel.resetFilter()
                this.findNavController().navigate(
                    AffectedCountriesFragmentDirections.actionAffectedCountriesFragmentToDetailFragment(
                        it
                    )
                )
            }
        })

        binding.edtSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(queryString: String?): Boolean {
                adapter.filter.filter(queryString)
                updateData()
                return false
            }

            override fun onQueryTextChange(queryString: String?): Boolean {
                if (queryString != "")
                    binding.imageView2.visibility = View.GONE
                else
                    binding.imageView2.visibility = View.VISIBLE
                adapter.filter.filter(queryString)
                updateData()
                return false
            }
        })

        return binding.root
    }

    private fun bindAdapter() {
        viewModel.countryData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                countryList.addAll(it)
                updateData()

                binding.listView.visibility = View.VISIBLE
                binding.loader.stop()
                binding.loader.visibility = View.GONE
            }
        })
    }

    private fun updateData() {
        adapter.mOldUser.clear()
        adapter.mOldUser.addAll(countryList)
    }

}