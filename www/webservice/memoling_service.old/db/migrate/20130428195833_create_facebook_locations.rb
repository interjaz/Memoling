class CreateFacebookLocations < ActiveRecord::Migration
  def change
    create_table :facebook_locations do |t|
      t.string :name

      t.timestamps
    end
  end
end
