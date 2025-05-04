from flask import Blueprint, request, jsonify
import requests
from utils import hash_password, verify_password, generate_token

user_bp = Blueprint('user_bp', __name__)
dao_url = "http://localhost:5600"


@user_bp.route('/v1/service/register', methods=['POST'])
def register():
    print("SERVICE >>> Envoi vers DAO pour inscription")

    #print("Requête reçue : ", request.get_json())
    data = request.get_json()
    #print("Requête reçue (DAO) avec debug_id :", data.get("debug_id"))

    # Continuer l'inscription
    data['password'] = hash_password(data['password'])  # hachage avant envoi
    try:
        response = requests.post(f"{dao_url}/v1/dao/user", json=data)
        if response.status_code == 201:
            return jsonify({'message': 'Inscription réussie'}), 201
        else:
            return jsonify(response.json()), response.status_code
    except Exception as e:
        print("Erreur lors de l'inscription :", e)
        return jsonify({'error': 'Erreur lors de l’inscription'}), 500


@user_bp.route('/v1/service/login', methods=['POST'])
def login():

    data = request.get_json()
   #print("Requête reçue depuis Android : ", data)
    email = data['email']
    password = data['password']

    dao_resp = requests.post(f"{dao_url}/v1/dao/user/login", json={"email": email})

    if dao_resp.status_code == 200:
        user = dao_resp.json()
        if verify_password(password, user['password']):
            token = generate_token(user['user_id'])
            return jsonify({'message': 'Connecte',
                            'user_id': user['user_id'],
                            'first_name' : user['first_name'],
                            'last_name': user['last_name'],
                            'sex': user['sex'],
                            'token': token}), 200
        else:
            return jsonify({'message': 'Mot de passe incorrect'}), 401
    else:
        return jsonify({'message': 'Utilisateur non trouvé'}), 404