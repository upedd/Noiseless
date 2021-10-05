package dev.uped.noiseless.di

import android.annotation.SuppressLint
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.uped.noiseless.data.measurement.datasource.LocalDataSource
import dev.uped.noiseless.data.measurement.datasource.LocalDataSourceImpl
import dev.uped.noiseless.data.measurement.datasource.RemoteDataSource
import dev.uped.noiseless.data.measurement.datasource.firestore.FirestoreDataSource
import dev.uped.noiseless.data.measurement.repository.MeasurementRepository
import dev.uped.noiseless.data.measurement.repository.MeasurementRepositoryImpl
import dev.uped.noiseless.home.HomeScreenViewModel
import dev.uped.noiseless.service.geocoding.DefaultGeocodingService
import dev.uped.noiseless.service.geocoding.GeocodingService
import dev.uped.noiseless.service.location.GmsLocationService
import dev.uped.noiseless.service.location.LocationService
import dev.uped.noiseless.ui.screen.MeasurementListScreenViewModel
import dev.uped.noiseless.ui.screen.MeasurementMapScreenViewModel
import dev.uped.noiseless.ui.screen.MeasurementResultViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.*

@SuppressLint("VisibleForTests")
val appModule = module {
    single { Firebase.firestore }
    single<RemoteDataSource> { FirestoreDataSource(get()) }
    single<LocalDataSource> { LocalDataSourceImpl() }
    single<MeasurementRepository> { MeasurementRepositoryImpl(get(), get()) }

    single { Geocoder(androidContext(), Locale.getDefault()) }
    single<LocationService> { GmsLocationService(FusedLocationProviderClient(androidContext())) }
    single<GeocodingService> { DefaultGeocodingService(get()) }

    viewModel { HomeScreenViewModel(get()) }
    viewModel { MeasurementMapScreenViewModel(get()) }
    viewModel { MeasurementListScreenViewModel(get()) }
    viewModel { MeasurementResultViewModel(get()) }
}