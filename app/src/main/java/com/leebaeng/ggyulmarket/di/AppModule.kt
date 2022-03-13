package com.leebaeng.ggyulmarket.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDBAdapter() = DBAdapter()

//    @Provides
//    @Singleton
//    fun provideUserDB() = Firebase.firestore.collection(DatabaseConst.FS_TABLE_USER)
//
//    @Provides
//    @Singleton
//    fun provideMarketDB() = Firebase.firestore.collection(DatabaseConst.FS_TABLE_MARKET)
//
//    @Provides
//    @Singleton
//    fun provideChatDB() = Firebase.database
//
//    @Provides
//    @Singleton
//    fun provideQueryMarketList() = provideUserDB().orderBy("createdAt", Query.Direction.DESCENDING).limit(15)
}