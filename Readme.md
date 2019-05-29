# Delivery Mobile APP

### Business requirement
As an user I want to be able to see a list of my deliveries, including receivers photo and item description. I also want to be able to see the delivery location on the map and full description when I click on the delivery item.

### Libraries Used

  * Retrofit
  * Dagger2
  * Robolectric
  * Mockito
  * Picasso
  * Room
  * RxJava
  * Espresso
  * Paging

### User Requirements
- Retrieve list of deliveries from the API
- Display list of deliveries.
- Show details when user select an item in the list.
- Add marker on the map based on the provided lat/lng.

### Things done in the project
Project is developed using MVVM Clean architecture.
App is fetching the list of deliveries from the Network and saving them into the DB for local caching. Then the app displays the list from cache and request from server if needed. The list use a page size of 20 to fetch the list from server.

## How to compile
- Android Studio IDE (3.4.1)
- Android SDK (28)
- Change the "GOOGLE_MAPS_API_KEY" in the gradle.properties file to make the google map work. To generate an Android Key on Google Developer console with the package com.deliverapp and your keystore's SHA1
