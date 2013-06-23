class FacebookLocation < ActiveRecord::Base  
  alias_attribute :m_name, :name
  
  has_many :users, :class_name => FacebookUser, :foreign_key => :location_id
end
