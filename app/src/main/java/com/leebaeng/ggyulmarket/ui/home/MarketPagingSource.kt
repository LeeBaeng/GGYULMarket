package com.leebaeng.ggyulmarket.ui.home

import androidx.paging.PagingSource
import androidx.paging.PagingState

class MarketPagingSource : PagingSource<Int, Any>() {
    override fun getRefreshKey(state: PagingState<Int, Any>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Any> {
        TODO("Not yet implemented")
    }
}