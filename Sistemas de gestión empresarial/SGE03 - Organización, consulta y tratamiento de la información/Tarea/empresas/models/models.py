# -*- coding: utf-8 -*-

# from odoo import models, fields, api


# class empresas(models.Model):
#     _name = 'empresas.empresas'
#     _description = 'empresas.empresas'

#     name = fields.Char()
#     value = fields.Integer()
#     value2 = fields.Float(compute="_value_pc", store=True)
#     description = fields.Text()
#
#     @api.depends('value')
#     def _value_pc(self):
#         for record in self:
#             record.value2 = float(record.value) / 100

# Importar los paquetes de Odoo modelos, definición de los campos y definición de api
from odoo import models, fields, api

class empresa(models.Model):
    # Nombre para referenciarnos.
    _name = 'res.partner'
    # Descripción de la clase
    _description = 'Hereda la clase res.partner'
    # Heredamos de la clase res.partner
    _inherit = 'res.partner'
