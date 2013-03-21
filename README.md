SimpleSideDrawer is an android library to add a drawer navigation into your android application.
This library has high affinity with other libraries like ActionBarSherlock or etc.

#Easy to add your app a side menu
![Demo image01](https://lh6.googleusercontent.com/-O-AKV6vo4U4/UOHA9PjYA9I/AAAAAAAAQV4/gayCEMA9q9c/s720/simple_side_drawer01.png) ![Demo image02](https://lh5.googleusercontent.com/-hoVDio62tgc/UOHBBU7K0LI/AAAAAAAAQWE/rEkvI2NgNl4/s720/simple_side_drawer02.png)

###Step1: Add the library
Add the jar library to your application project

###Step2: Set up the side menu
Add the 2 lines under the onCreate method in an Activity you want to add the side menu.

	protected void onCreate(Bundle data) {
		super.onCreate( data );
		setContentView( R.layout.main );
		
		mSlidingMenu = new SimpleSideDrawer( this );
		mSlidingMenu.setLeftBehindContentView( R.layout.behind_menu_left );
	}
	
The behind_menu.xml is the side menu layout. You can add it by calling setBehindeContentView method. That it you have to do for adding the side menu.
If you want to handle the widget on the side menu, you just call findViewById method as usual.

#### Need the right-side?

	mSlidingMenu.setRightBehindContentView( R.layout.behind_menu_right );

###Step3: Open/Close the side menu
You can easy open/close the side menu.

	mSlidingMenu.toggleDrawer();

Of course, a user can close the side menu by dragging the above view.

# More info

See my slide share in which I describe the detail of this library.
http://www.slideshare.net/adamrocker/simple-side-drawer2

#Directory

- SimpleSideDrawer: The library source code
- demo: The demo android projects which uses this library
- library: .jar file for easy to use
- doc: The document of this library

#Lisence
Copyright 2013, adamrocker ( http://www.adamrocker.com ).

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License.