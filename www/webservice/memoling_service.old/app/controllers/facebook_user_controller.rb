class FacebookUserController < ApplicationController

def index
 @users = FacebookUser.all
 
  respond_to do |format|
    format.html  # index.html.erb
    format.json  { render :json => @users, :include => [:location] }
  end
end

end
