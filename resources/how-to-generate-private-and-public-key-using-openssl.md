## 1️⃣ Check OpenSSL is available

```bash
openssl version
```

If it prints a version, you’re good to go ✅
(If not, install via Homebrew: `brew install openssl`)

---

## 2️⃣ Generate an RSA **Private Key**

This creates a **2048-bit RSA private key** (recommended minimum).

```bash
openssl genpkey -algorithm RSA \
  -out private_key.pem \
  -pkeyopt rsa_keygen_bits:2048
```

📌 Output:

* `private_key.pem` → **KEEP THIS SECRET**

To protect it with a passphrase:

```bash
openssl genpkey -algorithm RSA \
  -aes256 \
  -out private_key.pem \
  -pkeyopt rsa_keygen_bits:2048
```

---

## 3️⃣ Generate the **Public Key** from the Private Key

```bash
openssl rsa -pubout \
  -in private_key.pem \
  -out public_key.pem
```

📌 Output:

* `public_key.pem` → Safe to share

---

## 4️⃣ Verify the Keys (Optional but Recommended)

### View private key details

```bash
openssl rsa -in private_key.pem -check
```

### View public key details

```bash
openssl rsa -pubin -in public_key.pem -text -noout
```

---

## 5️⃣ Test the Key Pair (Optional)

### Encrypt with public key

```bash
echo "Hello RSA" | \
openssl pkeyutl -encrypt -pubin \
  -inkey public_key.pem > encrypted.bin
```

### Decrypt with private key

```bash
openssl pkeyutl -decrypt \
  -inkey private_key.pem \
  -in encrypted.bin
```

✔️ If it prints `Hello RSA`, your keys work correctly.

---

## 6️⃣ Common Key Sizes (Best Practice)

| Use Case      | Key Size |
| ------------- | -------- |
| Dev / Testing | 2048     |
| Production    | 3072     |
| High Security | 4096     |

Example for 4096-bit:

```bash
openssl genpkey -algorithm RSA \
  -out private_key.pem \
  -pkeyopt rsa_keygen_bits:4096
```

---

## 7️⃣ Where These Keys Are Used (Real-World)

As a Java backend developer, you’ll commonly use RSA keys for:

* 🔐 **JWT signing (RS256)**
* 🔑 **OAuth2 / OpenID Connect**
* 🌐 **TLS / SSL certificates**
* 🔄 **Secure key exchange**

Spring Boot typically loads them like:

```properties
rsa.private-key=classpath:private_key.pem
rsa.public-key=classpath:public_key.pem
```

---

## ⚠️ Important Security Notes

* ❌ Never commit `private_key.pem` to Git
* ✅ Use `.gitignore`
* 🔐 Use passphrase in production
* 🔁 Rotate keys periodically
