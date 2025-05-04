import jwt
import datetime

SECRET_KEY = "test_secret_key"

# Création d’un payload simple
payload = {
    "user_id": 123,
    "exp": datetime.datetime.utcnow() + datetime.timedelta(minutes=5)
}

# Essayons d’encoder et de décoder le token
try:
    # Générer le token JWT
    token = jwt.encode(payload, SECRET_KEY, algorithm="HS256")
    print(f" Token généré : {token}\n")

    # Décoder le token
    decoded = jwt.decode(token, SECRET_KEY, algorithms=["HS256"])
    print(" Token décodé avec succès :")
    print(decoded)

except AttributeError as e:
    print(" Erreur : PyJWT n'est probablement pas installé.")
    print(" Solution : pip uninstall jwt && pip install PyJWT")
    print(e)

except Exception as e:
    print(" Autre erreur lors de la manipulation du token JWT :")
    print(e)
