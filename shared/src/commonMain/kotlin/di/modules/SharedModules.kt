package di.modules

import data.PreferencesRepositoryImpl
import data.ParkingSignsRepositoryImpl
import domain.repositories.PreferencesRepository
import domain.repositories.ParkingSignsRepository
import org.koin.dsl.module

val sharedModules = module {
    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }
    single<ParkingSignsRepository> { ParkingSignsRepositoryImpl(get())}
}