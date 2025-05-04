class User:
    def __init__(self, last_name, first_name, sex, age, email, password, uid=0):
        self.uid = uid
        self.last_name = last_name
        self.first_name = first_name
        self.sex = sex
        self.age = age
        self.email = email
        self.password = password

    def __str__(self):
        return f'{self.first_name} {self.last_name} ({self.age} ans)'