package di.modules

import data.ParkingSignsRepositoryImpl
import domain.repositories.ParkingSignsRepository
import io.ktor.client.HttpClient
import org.koin.dsl.module

val sharedModules = module {
    single { HttpClient() }
    single<ParkingSignsRepository> { ParkingSignsRepositoryImpl(get())}
}