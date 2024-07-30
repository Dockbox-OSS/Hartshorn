# Security Policy
## JAR signing (as of July 2024)
All JAR files released on Maven Central are signed with the following GPG key:
```plaintext
Key ID: build@dockbox.org
Fingerprint: 6F34 6237 5920 3A7C 0E97 1D9E 979A 72D9 DFAB F6A2
Key size: RSA 4096
Date: 2024-07-30T16:53:39Z
```

You can import this key using the public key server:
```shell
$ gpg --keyserver keyserver.ubuntu.com --recv 6F34623759203A7C0E971D9E979A72D9DFABF6A2
```

## JAR signing (before July 2024)
JAR files released before July 2024 (up to and including release 0.5.0) were signed with the following GPG key:
```plaintext
Key ID: guuslieben@xendox.com
Fingerprint: CCC8 A24E D300 4EF6 22DE 752E F180 83C0 F99D 2EFB
Key size: RSA 4096
Date: 2021-11-11T18:13:20Z
```

```shell
$ gpg --keyserver keyserver.ubuntu.com --recv CCC8A24ED3004EF622DE752EF18083C0F99D2EFB
```

> [!NOTE]
> You can find a permanent copy of this notice at https://dockbox.org/gpg-key-hartshorn.txt
