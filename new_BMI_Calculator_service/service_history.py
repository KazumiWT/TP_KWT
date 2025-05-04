from flask import Blueprint, request, jsonify
import requests
from utils import calculer_bmi, classifier_bmi, check_token

history_bp = Blueprint('history_bp', __name__)
dao_url = "http://localhost:5600"

@history_bp.route('/v1/service/bmi', methods=['POST'])
def calc_bmi():
    # Vérifier le token
    user_token_id, error_response, status = check_token()
    if error_response:
        return error_response, status

    data = request.get_json()

    # Vérifier que le token appartient bien à l'utilisateur
    if user_token_id != data.get('user_id'):
        return jsonify({'message': 'Utilisateur non autorisé'}), 403

    bmi = calculer_bmi(data['weight'], data['height'])
    classification, new_measure = classifier_bmi(bmi, data['waist'], data['sex'])

    history = {
        "user_id": data['user_id'],
        "weight": data['weight'],
        "height": data['height'],
        "waist": data['waist'],
        "bmi": bmi,
        "bmi_classification": classification,
        "new_measure": new_measure
    }

    response = requests.post(f"{dao_url}/v1/dao/history", json=history)

    return jsonify({
        "bmi": bmi,
        "classification": classification,
        "new_measure": new_measure,
        "history_saved": response.status_code == 201
    }), 200

@history_bp.route('/v1/service/history/<int:user_id>', methods=['GET'])
def get_history(user_id):
    user_token_id, error_response, status = check_token()
    if error_response:
        return error_response, status

    if user_token_id != user_id:
        return jsonify({'message': "Accès interdit : mauvais utilisateur"}), 403
    response = requests.get(f"{dao_url}/v1/dao/history/{user_id}")
    return jsonify(response.json()), response.status_code


@history_bp.route('/v1/service/history/<int:history_id>', methods=['DELETE'])
def delete_history(history_id):
    user_token_id, error_response, status = check_token()
    if error_response:
        return error_response, status

    #  Vérifier que l’entrée existe et appartient bien à l’utilisateur
    check_resp = requests.get(f"{dao_url}/v1/dao/history/entry/{history_id}")
    if check_resp.status_code != 200:
        return jsonify({'message': 'Entrée déjà supprimée ou introuvable'}), 200

    owner_id = check_resp.json()['user_id']
    if owner_id != user_token_id:
        return jsonify({'message': "Vous n'avez pas le droit de supprimer cette entree"}), 403


    response = requests.delete(f"{dao_url}/v1/dao/history/{history_id}")
    return jsonify(response.json()), response.status_code