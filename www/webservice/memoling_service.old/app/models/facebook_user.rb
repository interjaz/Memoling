class FacebookUser < ActiveRecord::Base
  attr_accessible :firstName, :gender, :hometown, :lastName, :link, :locale, :name, :timezone, :updatedTime, :username, :verified 
  
  belongs_to :location, :class_name => "FacebookLocation"
end
