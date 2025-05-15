# Instal·lem python
## sudo apt install python3.10-venv -y

# Crear entorn virtual 
python3 -m venv venv

# Activar entorn virtual
source venv/bin/activate

# Instal·lar dependències
pip install -r requirements.txt

# Generar la documentació
mkdocs serve