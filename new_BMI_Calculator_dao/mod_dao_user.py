from flask import Blueprint, request, jsonify
from db_config import creer_connexion
from user import User

dao_user = Blueprint('dao_user', __name__)

# Route pour créer un nouvel utilisateur
@dao_user.route('/v1/dao/user', methods=['POST'])
def inserer_user():
    data = request.get_json()
    print("Requête reçue (DAO) :", data)

    # Vérifier que tous les champs requis sont présents et non vides
    required_fields = ["last_name", "first_name", "sex", "age", "email", "password"]
    for field in required_fields:
        if not data.get(field):
            return jsonify({'error': f'Champ manquant ou vide : {field}'}), 400

    # Nettoyage de l'email (évite les doublons avec majuscules/espaces)
    email = data.get("email").strip().lower()

    try:
        user = User(
            last_name=data.get("last_name"),
            first_name=data.get("first_name"),
            sex=data.get("sex"),
            age=data.get("age"),
            email=email,
            password=data.get("password")
        )
    except Exception as e:
        return jsonify({'error': f'Erreur lors de la création de l\'objet User : {str(e)}'}), 400

    conn = creer_connexion()
    cur = conn.cursor()
    try:
        cur.execute('''
            INSERT INTO user (last_name, first_name, sex, age, email, password)
            VALUES (%s, %s, %s, %s, %s, %s)
        ''', (user.last_name, user.first_name, user.sex, user.age, user.email, user.password))
        conn.commit()
        user_id = cur.lastrowid
        return jsonify({'message': 'Utilisateur inscrit avec succès', 'user_id': user_id}), 201
    except Exception as e:
        conn.rollback()
        print("Erreur DAO:", e)
        if "Duplicate entry" in str(e):
            print("DAO >>> Inscription déjà traitée, doublon ignoré")
            return jsonify({'message': 'Utilisateur déjà inscrit (doublon ignoré)'}), 200
    finally:
        conn.close()


# Route pour obtenir les infos de connexion
@dao_user.route('/v1/dao/user/login', methods=['POST'])
def login_user():
    data = request.get_json()
    email = data.get('email')
    if not email:
        return jsonify({'error': 'Email manquant'}), 400

    conn = creer_connexion()
    cur = conn.cursor()
    cur.execute("SELECT id, first_name, last_name, sex, password FROM user WHERE email=%s", (email.strip().lower(),))
    result = cur.fetchone()
    conn.close()

    if result:
        return jsonify({
            'user_id': result[0],
            'first_name': result[1],
            'last_name': result[2],
            'sex': result[3],
            'password': result[4]
        }), 200
    else:
        return jsonify({'message': 'Utilisateur non trouvé'}), 404
