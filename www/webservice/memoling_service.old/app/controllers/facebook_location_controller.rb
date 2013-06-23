class FacebookLocationController < ApplicationController


def index
 @locations = FacebookLocation.all
 
  respond_to do |format|
    format.html  # index.html.erb
    format.json  { render :json => @locations }
  end
end

end
