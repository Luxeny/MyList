-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class *
-keep @dagger.hilt.android.AndroidEntryPoint class *

-keep class * extends androidx.room.RoomDatabase
-keep class androidx.room.RoomDatabase { *; }

-keep class com.example.mylist.core.domain.model.** { *; }
-keep class com.example.mylist.core.data.local.entity.** { *; }

-keep class io.appmetrica.analytics.** { *; }
-dontwarn io.appmetrica.analytics.**
