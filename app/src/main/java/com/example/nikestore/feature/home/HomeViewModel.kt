package com.example.nikestore.feature.home

import androidx.lifecycle.MutableLiveData
import com.example.nikestore.core.NikeCompletableObserver
import com.example.nikestore.core.NikeSingleObserver
import com.example.nikestore.core.NikeViewModel
import com.example.nikestore.core.asyncNetworkRequest
import com.example.nikestore.data.Banner
import com.example.nikestore.data.Product
import com.example.nikestore.data.SORT_LATEST
import com.example.nikestore.data.SORT_POPULAR
import com.example.nikestore.data.repository.banner.BannerRepository
import com.example.nikestore.data.repository.product.ProductRepository
import io.reactivex.schedulers.Schedulers

class HomeViewModel(
    private val productRepository: ProductRepository,
    bannerRepository: BannerRepository
) :
    NikeViewModel() {

    val latestProductsLiveData = MutableLiveData<List<Product>>()
    val popularProductsLiveData = MutableLiveData<List<Product>>()
    val bannerLiveData = MutableLiveData<List<Banner>>()


    init {

        progressLiveData.value = true


        bannerRepository.getBanner()
            .asyncNetworkRequest()
            .doFinally { progressLiveData.value = false }
            .subscribe(object : NikeSingleObserver<List<Banner>>(compositeDisposable) {

                override fun onSuccess(t: List<Banner>) {
                    bannerLiveData.value = t
                }

            })

        productRepository.getProducts(SORT_LATEST)
            .asyncNetworkRequest()
            .subscribe(object : NikeSingleObserver<List<Product>>(compositeDisposable) {

                override fun onSuccess(t: List<Product>) {
                    latestProductsLiveData.value = t
                }

            })

        productRepository.getProducts(SORT_POPULAR)
            .asyncNetworkRequest()
            .subscribe(object : NikeSingleObserver<List<Product>>(compositeDisposable) {
                override fun onSuccess(t: List<Product>) {
                    popularProductsLiveData.value = t
                }

            })

    }

    fun addProductToFavorites(product: Product) {

        if (product.isFavorite)
            productRepository.removeFromFavoriteProducts(product)
                .subscribeOn(Schedulers.io())
                .subscribe(object : NikeCompletableObserver(compositeDisposable) {
                    override fun onComplete() {
                        product.isFavorite = false
                    }

                })

        else
            productRepository.addToFavoriteProducts(product)
                .subscribeOn(Schedulers.io())
                .subscribe(object : NikeCompletableObserver(compositeDisposable){

                    override fun onComplete() {
                        product.isFavorite = true
                    }

                })
    }
}