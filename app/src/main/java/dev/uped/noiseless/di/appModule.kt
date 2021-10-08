package dev.uped.noiseless.di

import android.annotation.SuppressLint
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dev.uped.noiseless.DATABASE_FILE_NAME
import dev.uped.noiseless.Database
import dev.uped.noiseless.data.datasource.LocalDataSource
import dev.uped.noiseless.data.datasource.LocalDatabaseDataSource
import dev.uped.noiseless.data.datasource.RemoteDataSource
import dev.uped.noiseless.data.datasource.firestore.FirestoreDataSource
import dev.uped.noiseless.data.repository.MeasurementRepository
import dev.uped.noiseless.data.repository.MeasurementRepositoryImpl
import dev.uped.noiseless.screen.home.HomeScreenViewModel
import dev.uped.noiseless.service.geocoding.DefaultGeocodingService
import dev.uped.noiseless.service.geocoding.GeocodingService
import dev.uped.noiseless.service.location.GmsLocationService
import dev.uped.noiseless.service.location.LocationService
import dev.uped.noiseless.screen.list.MeasurementListScreenViewModel
import dev.uped.noiseless.screen.MeasurementMapScreenViewModel
import dev.uped.noiseless.screen.result.MeasurementResultViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.*

@SuppressLint("VisibleForTests")
val appModule = module {
    single { Firebase.firestore }
    single<SqlDriver> { AndroidSqliteDriver(Database.Schema, androidContext(), DATABASE_FILE_NAME) }
    single { Database(get()) }
    single { get<Database>().measurementQueries }


    single<RemoteDataSource> { FirestoreDataSource(get()) }
    single<LocalDataSource> { LocalDatabaseDataSource(get()) }
    single<MeasurementRepository> { MeasurementRepositoryImpl(get(), get()) }

    single { Geocoder(androidContext(), Locale.getDefault()) }
    single<LocationService> { GmsLocationService(FusedLocationProviderClient(androidContext())) }
    single<GeocodingService> { DefaultGeocodingService(get()) }

    viewModel { HomeScreenViewModel(get()) }
    viewModel { MeasurementMapScreenViewModel(get()) }
    viewModel { MeasurementListScreenViewModel(get()) }
    viewModel { MeasurementResultViewModel(get()) }
}