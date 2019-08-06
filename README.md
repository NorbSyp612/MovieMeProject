Description 

MovieMe – For when you need an instant movie recommendation.

MovieMe connects you to the TheMovieDatabase (TMDb) and allows you to explore and find your favorite movies. After creating a collection of favorite movies, you can simply press the “MovieMe” button and a movie based on your favorites will be suggested for you!

Intended User

This app is for anyone who is interested in movies. It can be used to browse movies in different categories, find trailers, find reviews, and even suggest a movie to watch. It’s primarily intended for individuals who need a new movie suggestion quickly. 

Features

This app will include:
●	Allows users to browse TMDb using different categories and queries.
●	Stores users’ favorites in a Room database which can also be accessed offline.
●	Suggests a random movie using a custom algorithm based on the stored favorites.
●	Embeds YouTube trailers into the detail screen.
●	Widget that provides the “MovieMe” function on demand outside of the app.
●	RTL switching on all layouts.
●	Developed using industry standards for accessibility. All strings will be stored in the strings.xml file, includes content descriptions, and navigation via d-pad.
●	The app performs a short duration, on-demand request using an AsyncTask to the movie database each time the user chooses a new movie category or presses the “MovieMe” button.
●	Incorporates firebase messaging so that I may send users a notification message notifying them of a new version.

Programming Language

This app will be written solely in Java, as Kotlin not accepted at this time.

Libraries, Gradle, and Android Studio

This app will use Grade version 4.10.1. It will be developed in Android Studio 3.4.1. It will use the following grade libraries:

●	com.android.support:appcompat-v7:28.0.0
●	com.android.support:constraint-layout:1.1.3
●	com.android.support:recyclerview-v7:28.0.0
●	com.squareup.picasso:picasso:2.5.2
●	com.google.android.gms:play-services-ads:18.0.0
●	com.google.firebase:firebase-messaging:19.0.1
