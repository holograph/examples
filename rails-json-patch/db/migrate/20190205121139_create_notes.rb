class CreateNotes < ActiveRecord::Migration[5.2]
  def change
    create_table :notes do |t|
      t.text :title
      t.binary :content

      t.timestamps
    end
  end
end
