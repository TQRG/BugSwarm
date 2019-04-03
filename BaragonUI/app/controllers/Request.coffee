Controller = require './Controller'

RequestView = require '../views/request'

Request = require '../models/Request'

class RequestController extends Controller

    initialize: ({@requestId}) ->
        app.showPageLoader()

        @models.request = new Request {@requestId}

        @setView new RequestView
            model: @models.request

        app.showView @view

        @refresh()

    refresh: ->
        @models.request.fetch()

module.exports = RequestController
