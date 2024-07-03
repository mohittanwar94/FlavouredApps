package com.qureka.skool.di

import android.content.Context
import com.qureka.skool.CredentialHelper
//import com.qureka.skool.data.datasource.TaskDataSource
import com.qureka.skool.fgp.repository.FirstGamePlayRepository
import com.qureka.skool.ipchecker.repository.IpCheckerRepository
import com.qureka.skool.masterdata.GlobalConfigRepository
import com.qureka.skool.network.ApiClient
import com.qureka.skool.network.ApiInterface
import com.qureka.skool.utils.Utils.sha256
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofit(token: String): Retrofit {
        return ApiClient.getwithToken(token)
    }

    @Provides
    fun provideApiInterface(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }

    @Provides
    fun provideToken(@ApplicationContext application: Context): String {
        return (CredentialHelper.globalConfigAppKey() + application.packageName).sha256()
    }

    @Singleton
    @Provides
    fun provideGlobalConfigRepository(apiInterface: ApiInterface): GlobalConfigRepository {
        return GlobalConfigRepository(apiInterface)
    }

    @Singleton
    @Provides
    fun provideFirstGamePlayRepository(apiInterface: ApiInterface): FirstGamePlayRepository {
        return FirstGamePlayRepository(apiInterface)
    }

    @Singleton
    @Provides
    fun provideIpCheckerRepository(apiInterface: ApiInterface): IpCheckerRepository {
        return IpCheckerRepository(apiInterface)
    }


}