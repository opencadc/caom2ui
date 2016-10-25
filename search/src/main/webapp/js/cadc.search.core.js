(function ($)
{
  $.extend(true, window,
           {
             "ca": {
               "nrc": {
                 "cadc": {
                   "search": {
                     "Event": Event,
                     "EventData": EventData
                   }
                 }
               }
             }
           });
  /***
   * An event object for passing data to event handlers and letting them control propagation.
   * <p>This is pretty much identical to how W3C and jQuery implement events.</p>
   * @class EventData
   * @constructor
   */
  function EventData()
  {
    var isPropagationStopped = false;
    var isImmediatePropagationStopped = false;

    /***
     * Stops event from propagating up the DOM tree.
     * @method stopPropagation
     */
    this.stopPropagation = function ()
    {
      isPropagationStopped = true;
    };

    /***
     * Returns whether stopPropagation was called on this event object.
     * @method isPropagationStopped
     * @return {Boolean}
     */
    this.isPropagationStopped = function ()
    {
      return isPropagationStopped;
    };

    /***
     * Prevents the rest of the handlers from being executed.
     * @method stopImmediatePropagation
     */
    this.stopImmediatePropagation = function ()
    {
      isImmediatePropagationStopped = true;
    };

    /***
     * Returns whether stopImmediatePropagation was called on this event object.\
     * @method isImmediatePropagationStopped
     * @return {Boolean}
     */
    this.isImmediatePropagationStopped = function ()
    {
      return isImmediatePropagationStopped;
    }
  }

  /***
   * A simple publisher-subscriber implementation.
   *
   * @class Event
   * @param _name   Event name.
   * @constructor
   */
  function Event(_name)
  {
    var handlers = [];
    this.name = _name;

    /***
     * Adds an event handler to be called when the event is fired.
     * <p>Event handler will receive two arguments - an <code>EventData</code> and the <code>data</code>
     * object the event was fired with.<p>
     * @method subscribe
     * @param fn {Function} Event handler.
     */
    this.subscribe = function (fn)
    {
      handlers.push(fn);
    };

    /***
     * Removes an event handler added with <code>subscribe(fn)</code>.
     * @method unsubscribe
     * @param fn {Function} Event handler to be removed.
     */
    this.unsubscribe = function (fn)
    {
      for (var i = handlers.length - 1; i >= 0; i--)
      {
        if (handlers[i] === fn)
        {
          handlers.splice(i, 1);
        }
      }
    };

    this.getName = function()
    {
      return this.name;
    };

    /***
     * Fires an event notifying all subscribers.
     * @method notify
     * @param args {Object} Additional data object to be passed to all handlers.
     * @param e {EventData}
     *      Optional.
     *      An <code>EventData</code> object to be passed to all handlers.
     *      For DOM events, an existing W3C/jQuery event object can be passed in.
     * @param scope {Object}
     *      Optional.
     *      The scope ("this") within which the handler will be executed.
     *      If not specified, the scope will be set to the <code>Event</code> instance.
     */
    this.notify = function(args, e, scope)
    {
      var eventData = e || new EventData();
      var thisScope = scope || this;

      var returnValue;
      for (var i = 0; (i < handlers.length)
                      && !(eventData.isPropagationStopped()
                           || eventData.isImmediatePropagationStopped()); i++)
      {
        returnValue = handlers[i].call(thisScope, eventData, args);
      }

      return returnValue;
    };
  }
})(jQuery);