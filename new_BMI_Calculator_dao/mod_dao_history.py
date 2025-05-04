from flask import Blueprint, request, jsonify
from db_config import creer_connexion
from history import History
from datetime import datetime

dao_history = Blueprint('dao_history', __name__)

@dao_history.route('/v1/dao/history', methods=['POST'])
def inserer_history():
    data = request.get_json()
    history = History(**data)

    conn = creer_connexion()
    cur = conn.cursor()
    cur.execute('''
        INSERT INTO history (user_id, weight, height, waist, bmi, bmi_classification, new_measure, date)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
    ''', (
        history.user_id,
        history.weight,
        history.height,
        history.waist,
        history.bmi,
        history.bmi_classification,
        history.new_measure,
        history.date or datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    ))
    conn.commit()
    conn.close()
    return jsonify({'message': 'Historique ajouté'}), 201

#récupérer une entrée historique par son ID
@dao_history.route('/v1/dao/history/entry/<int:history_id>', methods=['GET'])
def get_history_entry(history_id):
    conn = creer_connexion()
    cur = conn.cursor()
    cur.execute("SELECT user_id FROM history WHERE history_id = %s", (history_id,))
    result = cur.fetchone()
    conn.close()

    if result:
        return jsonify({'user_id': result[0]}), 200
    else:
        return jsonify({'message': 'Entree non trouvee'}), 404

#récuperer historiques par user_id
@dao_history.route('/v1/dao/history/<int:user_id>', methods=['GET'])
def get_history(user_id):
    conn = creer_connexion()
    cur = conn.cursor()
    cur.execute("SELECT * FROM history WHERE user_id=%s ORDER BY date DESC", (user_id,))
    records = cur.fetchall()
    conn.close()

    historique = []
    for row in records:
        historique.append({
            'history_id': row[0],
            'user_id': row[1],
            'weight': row[2],
            'height': row[3],
            'waist': row[4],
            'bmi': float(row[5]),
            'bmi_classification': row[6],
            'new_measure': row[7],
            'date': row[8].strftime("%Y-%m-%d %H:%M:%S")
        })
    return jsonify(historique), 200


@dao_history.route('/v1/dao/history/<int:history_id>', methods=['DELETE'])
def delete_history(history_id):
    conn = creer_connexion()
    cur = conn.cursor()
    cur.execute("DELETE FROM history WHERE history_id = %s", (history_id,))
    conn.commit()
    affected = cur.rowcount
    conn.close()

    if affected == 0:
        return jsonify({'message': 'Aucune entree trouvee'}), 404
    return jsonify({'message': 'Entree supprimee'}), 200
