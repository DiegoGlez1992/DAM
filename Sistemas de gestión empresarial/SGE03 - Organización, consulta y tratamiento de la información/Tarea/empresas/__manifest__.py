# -*- coding: utf-8 -*-
{
    # Nombre del módulo
    'name': "Empresas", 

    # Descripción corta
    'summary': """
        Gestion de empresas""", 

    # Descripción detallada
    'description': """
        Modulo para la gestion de empresas realizado para la tarea de SGE03.
    """, 

    # Información sobre el desarrollador
    'author': "Diego Gonzalez Garcia",
    'website': "https://www.dedigo.com",

    # Categories can be used to filter modules in modules listing
    # Check https://github.com/odoo/odoo/blob/16.0/odoo/addons/base/data/ir_module_category_data.xml
    # for the full list
    'category': 'Uncategorized',
    'version': '0.1',

    # any module necessary for this one to work correctly
    'depends': ['base'],

    # always loaded
    'data': [
        'security/ir.model.access.csv',
        'views/views.xml',
        'views/templates.xml',
    ],
    # only loaded in demonstration mode
    'demo': [
        'demo/demo.xml',
    ],
}
