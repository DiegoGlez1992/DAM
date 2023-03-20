# -*- coding: utf-8 -*-
# from odoo import http


# class Empresas(http.Controller):
#     @http.route('/empresas/empresas', auth='public')
#     def index(self, **kw):
#         return "Hello, world"

#     @http.route('/empresas/empresas/objects', auth='public')
#     def list(self, **kw):
#         return http.request.render('empresas.listing', {
#             'root': '/empresas/empresas',
#             'objects': http.request.env['empresas.empresas'].search([]),
#         })

#     @http.route('/empresas/empresas/objects/<model("empresas.empresas"):obj>', auth='public')
#     def object(self, obj, **kw):
#         return http.request.render('empresas.object', {
#             'object': obj
#         })
