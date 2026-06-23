# Карта реализации MyList

Документ описывает, **что реализовано в проекте** и **в каких файлах** это находится.

---

## Чистая архитектура

### Gradle-модули

| Модуль | Содержимое |
|--------|------------|
| `:core` | data, domain, Room, репозитории, Use Cases, Hilt-модули |
| `:app` | presentation (экраны, ViewModel), аналитика, фоновые задачи |

Зависимость односторонняя: `app` → `core`.

### Слои

| Слой | Путь | Назначение |
|------|------|------------|
| **data** | `core/src/main/java/.../core/data/` | Room, DAO, Entity, `MyListRepositoryImpl`, маппинг |
| **domain** | `core/src/main/java/.../core/domain/` | модели, интерфейс репозитория, Use Cases |
| **presentation** | `app/src/main/java/.../presentation/` | Compose-экраны, ViewModel |

### Use Cases

Все сценарии вынесены в отдельные классы (`core/.../domain/usecase/`):

- `GetCategoriesUseCase`, `AddCategoryUseCase`, `UpdateCategoryUseCase`, `DeleteCategoryUseCase`
- `GetItemsUseCase`, `AddItemUseCase`, `UpdateItemUseCase`, `DeleteItemUseCase`, `UpdateItemStatusUseCase`

ViewModel вызывают только Use Cases, без прямого доступа к БД.

### Репозиторий и маппинг Entity → Domain

| Компонент | Файл |
|-----------|------|
| Интерфейс репозитория | `core/.../domain/repository/MyListRepository.kt` |
| Реализация | `core/.../data/repository/MyListRepositoryImpl.kt` |
| Маппинг | `core/.../data/mapper/Mappers.kt` |

### Room

| Компонент | Файл |
|-----------|------|
| База данных | `core/.../data/local/MyListDatabase.kt` |
| DAO категорий | `core/.../data/local/dao/CategoryDao.kt` |
| DAO элементов | `core/.../data/local/dao/ItemDao.kt` |
| Entity | `core/.../data/local/entity/` |

### DI (Hilt)

| Компонент | Файл |
|-----------|------|
| Точка входа | `app/.../MyListApplication.kt` (`@HiltAndroidApp`) |
| Модули БД и DAO | `core/.../di/DatabaseModule.kt` |
| Модуль репозитория | `core/.../di/RepositoryModule.kt` |
| ViewModel | `@HiltViewModel` в `CategoriesViewModel`, `ItemsViewModel` |

---

## Фоновые задачи и сервисы

### WorkManager — периодическая задача

| Что | Где |
|-----|-----|
| Регистрация задачи | `app/.../MyListApplication.kt` → `setupWorkManager()` |
| Worker | `app/.../background/CleanupWorker.kt` |
| Ограничения | `Constraints.Builder().setRequiresBatteryNotLow(true)` в `MyListApplication.kt` |
| Политика | `ExistingPeriodicWorkPolicy.KEEP`, интервал 1 день |

`MyListApplication` реализует `Configuration.Provider` для корректной инициализации WorkManager вместе с AppMetrica.

### Android Service

| Что | Где |
|-----|-----|
| Сервис | `app/.../background/AppMonitorService.kt` |
| Запуск | `MyListApplication.onCreate()` → `startAppMonitorService()` |
| Манифест | `app/src/main/AndroidManifest.xml` |

Жизненный цикл: `onStartCommand` (возврат `START_STICKY`), `onBind` → `null`.

---

## Анимации в Jetpack Compose

| Анимация | Сценарий | Файл |
|----------|----------|------|
| `AnimatedVisibility` | Появление/скрытие поля поиска | `CategoriesScreen.kt`, `ItemsScreen.kt` |
| `animateContentSize` | Плавное изменение высоты карточек | `CategoriesScreen.kt` (`CategoryCard`), `ItemsScreen.kt` (`ItemCard`) |

---

## XML-разметка и интеграция Compose

| Компонент | Файл |
|-----------|------|
| XML-разметка экрана | `app/src/main/res/layout/activity_about.xml` |
| Activity | `app/.../presentation/about/AboutActivity.kt` |
| Встроенный Compose | `ComposeView` с `id/composeView` в XML |
| Compose-контент | `AboutContent()` в `AboutActivity.kt` |

**Передача данных XML → Compose:** строка описания читается из ресурсов (`R.string.about_description`) в `onCreate` и передаётся параметром в `AboutContent(description = ...)`. Заголовок и кнопка «Назад» остаются в XML.

**Обоснование подхода:** статичная шапка экрана в XML, динамический блок с описанием и версией — в Compose без дублирования разметки.

---

## Gradle: конфигурация сборок

Конфигурация: `app/build.gradle.kts`

### buildTypes

| Тип | Особенности |
|-----|-------------|
| `debug` | `applicationIdSuffix = ".debug"` |
| `release` | ProGuard/R8 (`isMinifyEnabled`, `isShrinkResources`), подпись `signingConfigs.release` |

Правила обфускации: `app/proguard-rules.pro`, `core/consumer-rules.pro`.

### productFlavors (`demo` / `full`)

| Параметр | `demo` | `full` |
|----------|--------|--------|
| `applicationIdSuffix` | `.demo` | `.full` |
| `API_URL` | `https://demo.example.com/api` | `https://api.example.com/v1` |
| `FULL_VERSION` | `false` | `true` |

Использование feature-флага: бейдж «PRO» на главном экране при `BuildConfig.FULL_VERSION` — `CategoriesScreen.kt`.

---

## Качество кода и UX

### Пользовательский сценарий

Категории → элементы со статусами («Хочу» / «В процессе» / «Готово») → CRUD через диалоги, поиск и сортировка.

### Состояния UI

| Состояние | Реализация |
|-----------|------------|
| Загрузка | `ListUiState.Loading` → `ListStateContent.kt` |
| Успех | `ListUiState.Success` |
| Ошибка + повтор | `ListUiState.Error` + кнопка «Повторить» |
| Пустой список | отдельный экран в `ListStateContent.kt` |

Модель состояния: `app/.../presentation/common/ListUiState.kt`  
Использование: `CategoriesViewModel`, `ItemsViewModel` → `CategoriesScreen`, `ItemsScreen`.

### Обработка ошибок

Ошибки операций сохранения показываются через **Snackbar** (`CategoriesScreen.kt`, `ItemsScreen.kt`).

---

## Yandex AppMetrica

### Подключение

| Компонент | Файл |
|-----------|------|
| SDK | `gradle/libs.versions.toml`, зависимость в `app/build.gradle.kts` |
| Ключ API | `APPMETRICA_API_KEY` в `local.properties` → `BuildConfig` |
| Инициализация | `app/.../analytics/AppAnalytics.kt` → `MyListApplication.onCreate()` |
| ProGuard | `app/proguard-rules.pro` |

### Кастомные события

Отправка из `AppAnalytics.kt`, вызовы из ViewModel:

| Событие | Где вызывается |
|---------|----------------|
| `app_opened` | `AppAnalytics.initialize()` |
| `category_created` | `CategoriesViewModel.addCategory()` |
| `category_updated` | `CategoriesViewModel.updateCategory()` |
| `category_deleted` | `CategoriesViewModel.deleteCategory()` |
| `items_screen_opened` | `ItemsViewModel.init` |
| `item_created` | `ItemsViewModel.addItem()` |
| `item_updated` | `ItemsViewModel.updateItem()` |
| `item_deleted` | `ItemsViewModel.deleteItem()` |
| `item_status_changed` | `ItemsViewModel.updateStatus()` |

### Handled-ошибки

`AppAnalytics.logHandledError()` → `AppMetrica.reportError()` при сбоях сохранения в `CategoriesViewModel` и `ItemsViewModel`.
