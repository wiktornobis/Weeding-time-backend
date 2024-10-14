## Środowisko deweloperskie

#### Na początek potrzebujesz:

* 8 GB RAMu
* ulubionego IDE
* klienta GIT skonfigurowanego do pracy z Twoim kluczem SSH
* Lokalnie zainstalowany docker


#### Źródło - projekt do pobrania

```bash
git clone git@github.com:wiktornobis/Weeding-time-backend.git
```


Baza danych psql jest zdockeryzowana.


1. W głównym katalogu projektu skopiować plik `.env.example` na `.env`. Zawiera on zmienne środowiskowe.
2. W katalogu /macos/docker/ - zbudować obraz kontenera docker - zbuduje się baza postgresql: `docker-compose up --build`
    * Po utworzeniu kontenerów i bazy danych można zalogować się lokalnie z terminala `docker exec -it {nazwa_bazy_danych} psql -U {nazwa_uzytkownika} -d {nazwa_bazy_danych}`
3. Przechodząc z głównej scieżki do `cd ./etc/initialDtat/sql` wykonać skrypt initData.sql (można dopasować swoje dane do admina)
4. Uruchamiając tomcata np. za pomocą Ideo w przeglądarce aplikacja powinna być widoczna pod adresem `http://localhost:8088`.





Chcemy, by lokalnie aplikację było widać pod adresem `weedingapp.local.pl`. W tym celu musimy ustawić reverse proxy. Poniżej
przykładowa konfiguracja Apache:

```apacheconf
<VirtualHost *:80>
  ProxyPreserveHost On
  ProxyRequests Off
  ServerName weedingapp.local
  ProxyPass / http://localhost:8088/
  ProxyPassReverse / http://localhost:8088/
</VirtualHost>
```

Trzeba też pamiętać o wpisie

```
127.0.0.1	weedingapp.local.pl
```

w pliku `/etc/hosts`.