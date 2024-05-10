package di.modules

import data.DisclaimerRepositoryImpl
import data.ParkingSignsRepositoryImpl
import domain.repositories.DisclaimerRepository
import domain.repositories.ParkingSignsRepository
import org.koin.dsl.module

val sharedModules = module {
    single<DisclaimerRepository> { DisclaimerRepositoryImpl(get()) }
    single<ParkingSignsRepository> { ParkingSignsRepositoryImpl(get())}
}