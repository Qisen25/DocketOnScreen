# DocketOnScreen

[![Project Status: WIP – Initial development is in progress, but there has not yet been a stable, usable release suitable for the public.](https://www.repostatus.org/badges/latest/wip.svg)](https://www.repostatus.org/#wip)

DocketOnScreen is a "take away" docket recording app built in Android/Kotlin. Although described as a "take away" docket, this app is suitable for anyone who 
may need to record sale orders of items on the go.

## Why build this?
Kinda built this for fun to see if building an app with one activity and multiple fragments really does provide a faster and responsive UI compared to having more than one
activity. So far this has met expectations. A simple but fast, responsive UI would be helpful in an environment where you are under pressure to take orders quickly. 

## Features
- Add, edit and remove menus/catalogues and its corresponding items. These are stored persistently via SQLite
- Access the menus to begin taking orders to add/remove items to cart, recording customer details and retrieve cost.
- Apply surcharge or discount to order. 
- Filter items in menu via tab and/or search term to allow ease when looking for items.
- Print to pdf or printer on wifi network.

## Future Features TODOS
- Specify order types.
- Make order view swipeable between tabs.
- More details about menus.
- Enable specification of item extra options.
- History of orders.
- Store and retrieve menus and items from a web service.
- Work with POS printers (can't be bothered buying a POS printer at the moment lol). Currently only tested with Canon mg6400 series.
