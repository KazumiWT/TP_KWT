from db_config import creer_tables

def main():
    try:
        creer_tables()
        print("Tables créées ou déjà existantes.")
    except Exception as e:
        print("Une erreur est survenue :", e)

#if __name__ == '__main__':
#    main()