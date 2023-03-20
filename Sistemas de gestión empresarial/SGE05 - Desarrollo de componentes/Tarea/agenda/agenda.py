# -*- coding: utf-8 -*-
# Importar los paquetes de Odoo modelos, definición de los campos y definición de api
from odoo import models, fields, api

# Clase Agenda
class agenda(models.Model):
    # Nombre para referenciarnos.
    _name = 'agenda'
    # Descripción de la clase
    _description = 'Agenda telefónica'
    # Campos
    nombre = fields.Char('nombre', required = True)
    telefono = fields.Char('telefono', required = True)
    observaciones = fields.Char('observaciones', required = False)
    