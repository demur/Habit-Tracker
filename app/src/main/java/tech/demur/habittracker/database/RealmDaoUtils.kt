@file:JvmName("RealmUtils")

package tech.demur.habittracker.database

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults

fun Realm.habitModel(): HabitDao = HabitDao(this)
fun Realm.periodModel(): PeriodDao = PeriodDao(this)
fun Realm.recordModel(): RecordDao = RecordDao(this)

fun <T : RealmModel> RealmResults<T>.asLiveData() = LiveRealmData<T>(this)