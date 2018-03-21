# DZwelg
App-gedeelte van het Dijlezonen kassaportaal.

## Google Services
Tijdens het compileren kan volgende fout optreden als google-services.json niet is gedownload: 
> File google-services.json is missing. The Google Services Plugin cannot function without it.

Gelieve volgende stappen te doorlopen om het probleem op te lossen:
1. Maak onder `/app/src` de mappen `dev` en `prod` aan (ter info: dit zijn de twee "[product flavors](https://developer.android.com/studio/build/build-variants.html#product-flavors)" gespecifieerd in app/build.gradle)
2. Download de `google-services.json` van Firebase in Settings.
    - [Voor dev (DZwelg-dev)](https://console.firebase.google.com/project/dzwelg-dev/settings/general/android:be.dijlezonen.dzwelg.dev)
    - [Voor prod (DZwelg)](https://console.firebase.google.com/project/dzwelg-b1f15/settings/general/android:be.dijlezonen.dzwelg)
3. Zet beide bestanden in hun respectievelijke mappen, m.a.w. het JSON-bestand van DZwelg-dev in `/app/src/dev/` en het JSON-bestand van DZwelg in `/app/src/prod/`
4. Gelieve deze bestanden **niet in te checken**, dit is immers een veiligheidsrisico (staan om deze reden ook in app/.gitignore).
