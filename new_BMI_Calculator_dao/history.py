from datetime import datetime

class History:
    def __init__(self, user_id, weight, height, waist, bmi, bmi_classification, new_measure, date=None, hid = 0):
        self.hid = hid
        self.user_id = user_id
        self.weight = weight
        self.height = height
        self.waist = waist
        self.bmi = bmi
        self.bmi_classification = bmi_classification
        self.new_measure = new_measure
        self.date = date or datetime.now().strftime("%Y-%m-%d %H:%M:%S")

